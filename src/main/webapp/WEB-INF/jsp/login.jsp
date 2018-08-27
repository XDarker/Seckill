<%@page contentType="text/html; charset=UTF-8" language="java" %>
<%@include file="common/tag.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>用户登录</title>
    <%@include file="common/head.jsp" %>
</head>
<body>

<form name="loginForm" id="loginForm" method="post"  style="width:50%; margin:0 auto">

    <h2 style="text-align:center; margin-bottom: 20px">用户登录</h2>

    <div class="form-group">
        <div class="row">
            <label class="form-label col-md-4">请输入手机号码</label>
            <div class="col-md-5">
                <input id="mobile" name = "mobile" class="form-control" type="text" placeholder="手机号码" required="true"  minlength="11" maxlength="11" />
            </div>
            <div class="col-md-1">
            </div>
        </div>
    </div>

    <div class="form-group">
        <div class="row">
            <label class="form-label col-md-4">请输入密码</label>
            <div class="col-md-5">
                <input id="password" name="password" class="form-control" type="password"  placeholder="密码" required="true" minlength="6" maxlength="16" />
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-5">
            <button class="btn btn-primary btn-block" type="reset" onclick="reset()">重置</button>
        </div>
        <div class="col-md-5">
            <button class="btn btn-primary btn-block" type="submit" onclick="login()">登录</button>
        </div>
    </div>

</form>
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

    function login(){
        $("#loginForm").validate({
            submitHandler:function(form){
                doLogin();
            }
        });
    }

    function doLogin(){
        g_showLoading();

        var inputPass = $("#password").val();
        var salt = g_passsword_salt;
        var str = inputPass+salt+"";
        var password = md5(str);

        $.ajax({
            url: "/user/do_login",
            type: "POST",
            data:{
                mobile:$("#mobile").val(),
                password: password
            },
            success:function(data){
                layer.closeAll();
                if(data.status == 0){
                    layer.msg("成功");
                    window.location.href="/seckill/list";
                    //window.location.href="/demo/thymeleaf";
                }else{
                    layer.msg(data.msg);
                }
            },
            error:function(){
                layer.closeAll();
            }
        });
    }
</script>
</html>