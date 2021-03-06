(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["loginRegistration"], {
    "07ac": function (t, e, a) {
        var r = a("23e7"), n = a("6f53").values;
        r({target: "Object", stat: !0}, {
            values: function (t) {
                return n(t)
            }
        })
    }, "08f9": function (t, e, a) {
        "use strict";
        a.r(e);
        var r = function () {
                var t = this, e = t.$createElement, a = t._self._c || e;
                return a("form", {
                    staticClass: "Login-Form Form", on: {
                        submit: function (e) {
                            return e.preventDefault(), t.onSubmit(e)
                        }
                    }
                }, [a("InputEmail", {
                    attrs: {error: t.authErrors.email},
                    on: {"field-validated": t.onValidateField}
                }), a("InputPassword", {
                    attrs: {error: t.authErrors.password},
                    on: {"field-validated": t.onValidateField}
                }), a("div", {staticClass: "Form-Name"}, [a("div", {staticClass: "Form-Row"}, [a("div", {staticClass: "Form-Label"}, [t._v(" Имя ")]), a("div", {staticClass: "Form-Value"}, [a("input", {
                    directives: [{
                        name: "model",
                        rawName: "v-model",
                        value: t.name,
                        expression: "name"
                    }],
                    staticClass: "Input",
                    class: {"Input--state_invalid": t.authErrors.name},
                    attrs: {type: "input", placeholder: ""},
                    domProps: {value: t.name},
                    on: {
                        input: function (e) {
                            e.target.composing || (t.name = e.target.value)
                        }
                    }
                }), t.authErrors.name ? a("div", {staticClass: "Login-Errors"}, [a("div", {staticClass: "Input-Error"}, [t._v(" " + t._s(t.authErrors.name) + " ")])]) : t._e()])])]), a("Captcha", {
                    attrs: {
                        src: t.image,
                        error: t.authErrors.captcha
                    }, on: {"field-validated": t.onValidateField}
                }), a("div", {staticClass: "Form-Submit"}, [a("BaseButton", {
                    attrs: {
                        onClickButton: t.onSubmit,
                        disabled: "success" !== t.submitStatus
                    }
                }, [t._v(" Зарегистрироваться ")])], 1)], 1)
            }, n = [], s = (a("99af"), a("b0c0"), a("b64b"), a("d3b7"), a("5530")), i = a("d860"), o = a("0d38"),
            u = function () {
                return a.e("baseButton").then(a.bind(null, "82ea"))
            }, c = function () {
                return a.e("inputEmail").then(a.bind(null, "994b"))
            }, l = function () {
                return a.e("captcha").then(a.bind(null, "e820"))
            }, d = function () {
                return a.e("inputPassword").then(a.bind(null, "6f60"))
            }, f = {
                components: {BaseButton: u, InputEmail: c, InputPassword: d, Captcha: l}, data: function () {
                    return {requiredFields: ["secret", "email", "password", "repeatPassword", "captcha"], name: ""}
                }, mixins: [i["a"], o["a"]], computed: {
                    authErrors: function () {
                        return this.$store.getters.authErrors
                    }
                }, methods: {
                    onSubmit: function () {
                        var t = this;
                        this.$store.dispatch("register", Object(s["a"])({}, this.validatedFields, {name: this.name})).then((function () {
                            Object.keys(t.authErrors).length || t.$router.push({name: "registration-success"})
                        })).catch((function (e) {
                            return t.serverErrors.push(e)
                        }))
                    }
                }, metaInfo: function () {
                    return {title: this.blogInfo ? "Регистрация | ".concat(this.blogInfo.title, " - ").concat(this.blogInfo.subtitle) : "Регистрация"}
                }
            }, h = f, m = a("2877"), v = Object(m["a"])(h, r, n, !1, null, null, null);
        e["default"] = v.exports
    }, "0d38": function (t, e, a) {
        "use strict";
        var r = a("bc3a"), n = a.n(r), s = a("8c89");
        e["a"] = {
            data: function () {
                return {image: "", captchaError: "", errors: []}
            }, mounted: function () {
                var t = this;
                n.a.get("".concat(s["a"], "/api/auth/captcha")).then((function (e) {
                    t.image = e.data.image, t.validatedFields.secret = e.data.secret
                })).catch((function (e) {
                    t.errors.push(e), t.validatedFields.secret = "no_secret"
                }))
            }
        }
    }, "6f53": function (t, e, a) {
        var r = a("83ab"), n = a("df75"), s = a("fc6a"), i = a("d1e7").f, o = function (t) {
            return function (e) {
                var a, o = s(e), u = n(o), c = u.length, l = 0, d = [];
                while (c > l) a = u[l++], r && !i.call(o, a) || d.push(t ? [a, o[a]] : o[a]);
                return d
            }
        };
        t.exports = {entries: o(!0), values: o(!1)}
    }, a623: function (t, e, a) {
        "use strict";
        var r = a("23e7"), n = a("b727").every, s = a("a640"), i = a("ae40"), o = s("every"), u = i("every");
        r({target: "Array", proto: !0, forced: !o || !u}, {
            every: function (t) {
                return n(this, t, arguments.length > 1 ? arguments[1] : void 0)
            }
        })
    }, d860: function (t, e, a) {
        "use strict";
        a("a623"), a("45fc"), a("b64b"), a("07ac");
        var r = a("5530");
        e["a"] = {
            data: function () {
                return {validatedFields: {}, serverErrors: []}
            }, computed: {
                submitStatus: function () {
                    var t = this, e = this.requiredFields.every((function (e) {
                        return Object.keys(t.validatedFields).some((function (t) {
                            return t === e
                        }))
                    })), a = Object.values(this.validatedFields).every((function (t) {
                        return !1 !== t
                    }));
                    return e && a ? "success" : "error"
                }
            }, methods: {
                onValidateField: function (t) {
                    this.validatedFields = Object(r["a"])({}, this.validatedFields, {}, t)
                }
            }
        }
    }
}]);
//# sourceMappingURL=loginRegistration.b8dc9138.js.map