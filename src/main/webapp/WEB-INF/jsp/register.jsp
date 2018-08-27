<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@include file="common/tag.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>用户注册</title>
    <%@include file="common/head.jsp" %>

    <style>
        body {
            margin-top: 20px;
            margin: 0 auto;
        }

        .carousel-inner .item img {
            width: 100%;
            height: 300px;
        }

        font {
            color: #3164af;
            font-size: 18px;
            font-weight: normal;
            padding: 0 10px;
        }

        .error{
            color:red
        }
    </style>
</head>

<body>

<div class="container"
     style="width: 100%;">
    <div class="row">
        <div class="col-md-2"></div>
        <div class="col-md-8"
             style="background: #fff; padding: 40px 80px; margin: 30px; border: 7px solid #ccc;">
            <font>会员注册</font>USER REGISTER
            <form name="myform" id="myform"  class="form-horizontal"  method="post" style="margin-top: 5px;">
                <div class="form-group">
                    <label for="nickname" class="col-sm-2 control-label">昵称</label>
                    <div class="col-sm-6">
                        <input type="text" class="form-control" id="nickname" name="nickname"
                               placeholder="请输入昵称"  minlength="2" maxlength="6">
                    </div>
                </div>
                <div class="form-group">
                    <label for="id" class="col-sm-2 control-label">手机号</label>
                    <div class="col-sm-6">
                        <input type="text" class="form-control" id="id" name="id"
                               placeholder="请输入手机号" minlength="11" maxlength="11" >
                    </div>
                </div>
                <div class="form-group">
                    <label for ="password" class="col-sm-2 control-label">密码</label>
                    <div class="col-sm-6">
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="请输入密码"  minlength="6" maxlength="16">
                    </div>
                </div>
                <div class="form-group">
                    <label for="confirmpwd" class="col-sm-2 control-label">确认密码</label>
                    <div class="col-sm-6">
                        <input type="password" class="form-control" id="confirmpwd" name="repassword"
                               placeholder="请输入确认密码">
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button class="btn btn-primary btn-block" type="submit" onclick="register()" style=" height: 35px; width: 100px;">注册</button>
                    </div>
                </div>
            </form>
        </div>

        <div class="col-md-2"></div>

    </div>
</div>
</body>
<%--jQery文件,务必在bootstrap.min.js之前引入--%>
<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<%--使用CDN 获取公共js http://www.bootcdn.cn/--%>
<script type="text/javascript" src="/resources/static/bootstrap/js/bootstrap.min.js"></script>
<!-- jquery-validator -->
<script type="text/javascript" src="/resources/static/jquery-validation/jquery.validate.min.js"></script>
<script type="text/javascript" src="/resources/static/jquery-validation/localization/messages_zh.min.js"></script>
<!-- layer -->
<script type="text/javascript" src="/resources/static/layer/layer.js"></script>
<!-- md5.js -->
<script type="text/javascript" src="/resources/static/js/md5.min.js"></script>
<!-- common.js -->
<script type="text/javascript" src="/resources/static/js/common.js"></script>

<script type="text/javascript">

    function register(){
        g_showLoading();

        var inputPass = $("#password").val();
        var salt = g_passsword_salt;
        var str = inputPass+salt+"";
        var password = md5(str);

        $.ajax({
            url: "/user/register",
            type: "POST",
            data:{
                id:$("#id").val(),
                nickname:$("#nickname").val(),
                password: password,
                salt:$("#salt").val()
            },
            success:function(data){
                layer.closeAll();
                if(data.status == 0){
                    layer.msg("成功");
                    window.location.href="/user/to_login";

                }else{
                    layer.msg(data.desc);
                }
            }
//            error:function(){
//                layer.closeAll();
//            }
        });
    }
//    function doRegister(){
//
//    }

    //自定义校验规则
    $.validator.addMethod(
        //规则的名称
        "id",
        //校验的函数
        function(value,element,params){

            //定义一个标志
            var flag = false;

            //value:输入的内容
            //element:被校验的元素对象
            //params：规则对应的参数值
            //目的：对输入的username进行ajax校验
            $.ajax({
                "async":false,
                "url":"/user/to_checkId",
                "data":{"id":value},
                "type":"POST",
                "dataType":"json",
                "success":function(data){
                    if(data.status == 1){
                        flag = true;
                    }

                }
            });


            //返回false代表该校验器不通过
            return !flag;
        }

    );


    $(function(){
        $("#myform").validate({
            rules:{
                "id":{
                    "required":true,
                    "id":true
                   // "rangelength":[11,11],
                },
                "password":{
                    "required":true,
                    "rangelength":[6,16]
                },
                "repassword":{
                    "required":true,
                    "rangelength":[6,16],
                    "equalTo":"#password"
                },
                "email":{
                    "required":true,
                    "email":true
                }

            },
            messages:{
                "id":{
                    "required":"手机号不能为空",
                    "id":"手机号已存在"
                },
                "password":{
                    "required":"密码不能为空",
                    "rangelength":"密码长度6-16位"
                },
                "repassword":{
                    "required":"密码不能为空",
                    "rangelength":"密码长度6-16位",
                    "equalTo":"两次密码不一致"
                },
                "email":{
                    "required":"邮箱不能为空",
                    "email":"邮箱格式不正确"
                }
            }
        });
    });

</script>
</html>