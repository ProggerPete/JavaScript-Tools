<%@ taglib uri="http://custom.tag.com/demo" prefix="cust"%>

<html>
    <head>
        <%--<script type="text/javascript" src="js/TestB.js"></script>--%>
        <%--<script type="text/javascript" src="js/test2.js"></script>--%>
        <%--<script type="text/javascript" src="js/test3.js"></script>--%>
        <%--<script type="text/javascript" src="js/test4.js"></script>--%>

			<%--<script type="text/javascript" src="jsResource/TestB.js"></script>--%>
		<script type="text/javascript">
			<jsp:include page="jsResource/TestB.js" />
		</script>
        <script type="text/javascript">
            BTest();
        </script>
    </head>
<body>
<h2>Hello World!</h2>
<%--<cust:jsimport/>--%>
</body>
</html>
