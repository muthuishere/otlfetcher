println """
<html>
    <head>
        <title>Example Groovy Servlet</title>
    </head>
    <body>
Hello from Groovy, ${request.remoteHost}: ${new Date()}
    </body>
</html>
"""