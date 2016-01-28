<%@include file="/apps/shiro-aem/global.jsp"%><%
%><%@page session="false" %>

<div class="row">
    <div class="mainColoumn">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Please sign in</h3>
            </div>
            <div class="panel-body">
                <form id="formID" name="loginform" action="#" method="POST" accept-charset="UTF-8" role="form">

                    <input class="form-control" placeholder="Username or Email" name="username" type="text">

                    <input class="form-control" placeholder="Password" name="password" type="password" value="">
                    <div class="checkbox">
                        <label>
                            <input name="rememberMe" type="checkbox" value="true"> Remember Me
                        </label>
                    </div>
                    <input class="btn btn-block" type="submit" value="Login">
                </form>
            </div>
            <div class="result">

            </div>
        </div>
    </div>
</div>


<script type="application/javascript">

    jQuery(document).ready(function () {
        jQuery( "#formID" ).submit(function( event ) {
            event.preventDefault();
            $.ajax({
                url: "/bin/shiroLogin",
                type: 'post',
                data: $("form").serialize(),
                success: function (data) {
                    console.info(data);
                    $('.result').text(data);
                    $('.panel-body').hide();
                }
            });
        });
    });
</script>
