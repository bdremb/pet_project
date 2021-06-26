package com.diplom.skillbox.blog_driver.service;

import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.imgscalr.Scalr.OP_ANTIALIAS;
import static org.imgscalr.Scalr.OP_BRIGHTER;
import static org.imgscalr.Scalr.resize;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.diplom.skillbox.blog_driver.exception.MaxImageSizeException;
import com.diplom.skillbox.blog_driver.exception.UnsupportedImageFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Service
public class ImageServiceImpl implements ImageService {
  final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);
  @Value("${upload.path}")
  private String uploadPath;
  @Value("${upload.max-image-width}")
  private double maxImageWidth;
  @Value("${upload.max-avatar-width}")
  private int maxAvatarWidth;
  @Value("${upload.max-image-size}")
  private int maxImageSize;
  private static final String IMAGE_FORMAT_JPG = "jpg";
  private static final String IMAGE_FORMAT = ".+\\.(jpe?g|gif|png)";
  private static final String IMAGE_RESOURCE_TYPE = "image";
  private static final int CHAR_A = 'A';
  private static final int CHAR_Z = 'Z';
  private static final int FOLDER_NAME_LENGTH = 2;
  private static final int COUNT_OF_SUBFOLDER = 3;
  private Map imageMap;

  private final Cloudinary cloudinary;

  public ImageServiceImpl(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

  @Override
  public String storeImage(MultipartFile image)
      throws UnsupportedImageFormatException, IOException, MaxImageSizeException {
    return getUrlImageCloudinary(image, maxImageWidth, uploadPath);
  }

  /**
   * The method checks that the input file matches the image in the .jpeg,
   * .jpg, .png format.  If  the  check  is  successful,  it  generates a
   * random  file  name,  saves  the  thumbnail  image  to  Cloudinary in
   * accordance with the "params" settings.
   *
   * @param image         MultipartFile with image
   * @param maxImageWidth - required image width
   * @param path          root folder name
   * @return link to image in Cloudinary
   * @throws UnsupportedImageFormatException if image has unsupported format
   * @throws IOException                     if image is not a valid
   * @link https://cloudinary.com/
   */
  @Override
  public String getUrlImageCloudinary(MultipartFile image, double maxImageWidth, String path)
      throws UnsupportedImageFormatException, IOException, MaxImageSizeException {
    validateImage(image);
    uploadImageToCloudinary(image, path, maxImageWidth);
    return imageMap.get("secure_url").toString();
  }

  private void uploadImageToCloudinary(MultipartFile image, String path, double maxImageWidth)
      throws IOException {
    Map params = ObjectUtils.asMap(
        "public_id", getUploadPathCloudinary(path) + randomUUID().toString(),
        "overwrite", true,
        "resource_type", IMAGE_RESOURCE_TYPE);
    imageMap = cloudinary.uploader().upload(getReductionImage(image, maxImageWidth), params);
  }

  /**
   * The method proportionally reduces images for further insertion into a
   * post or comment to a post.  If  the  required image size is less than
   * or  equal  to  the  size  of  the  user's  avatar,  then the image is
   * additionally cropped to a square shape.
   *
   * @param image         MultipartFile with image
   * @param maxImageWidth required image width
   * @return an image represented by an array of bytes
   * @throws IOException if image is not a valid
   */
  private byte[] getReductionImage(MultipartFile image, double maxImageWidth) throws IOException {
    var bufferedImage = ImageIO.read(image.getInputStream());
    if (bufferedImage.getWidth() > maxImageWidth) {
      int newHeight = (int) round(bufferedImage.getHeight() / (bufferedImage.getWidth() / maxImageWidth));
      bufferedImage = resize(bufferedImage, (int) maxImageWidth, newHeight, OP_ANTIALIAS, OP_BRIGHTER);
      if (maxImageWidth <= maxAvatarWidth) {
        int borderSize = min(bufferedImage.getWidth(), bufferedImage.getHeight());
        bufferedImage = bufferedImage.getSubimage(0, 0, borderSize, borderSize);
      }
    }
    var outputStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, IMAGE_FORMAT_JPG, outputStream);
    return outputStream.toByteArray();
  }

  /**
   * The method generates a path with three random subfolders. Each subfolder
   * name consists of two letters  of  the Latin alphabet.  The  name  of the
   * root subfolder is passed in the parameters of the method.
   *
   * @param uploadPath - root subfolder name
   * @return generated path with three random subfolders
   */
  private String getUploadPathCloudinary(String uploadPath) {
    var stringBuilder = new StringBuilder();
    for (int i = 0; i < COUNT_OF_SUBFOLDER; i++) {
      for (int j = 0; j < FOLDER_NAME_LENGTH; j++) {
        stringBuilder.append((char) (CHAR_A + (new Random().nextInt(CHAR_Z - CHAR_A))));
      }
      stringBuilder.append(File.pathSeparator);
    }
    return format("%s/%s", uploadPath, stringBuilder.toString());
  }

  private void validateImage(MultipartFile image)
      throws MaxImageSizeException, UnsupportedImageFormatException {
    if (image.getSize() > maxImageSize) {
      throw new MaxImageSizeException(String.format("Image size more than %d bytes", maxImageSize));
    } else if (!Objects.requireNonNull(image.getOriginalFilename()).toLowerCase().matches(IMAGE_FORMAT)) {
      logger.error("Image format {} is not supported.", image.getContentType());
      throw new UnsupportedImageFormatException(String.format("Image format is not matches %s.", IMAGE_FORMAT));
    }
  }
}