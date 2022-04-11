<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!doctype html>
<html>
<head>
    <title>Tours Details</title>
    tour Details for ${tour.tourName}
    <p>${tour.description}</p>
    <p>Schedule:</p>
    <table>
        <tr><th>Venue</th><th>Date</th></tr>
        <c:forEach var="concert" items="${tour.concerts}">
            <tr><td>${concert.venue}</td><td>${concert.type}</td>
                <td><fmt:formatDate pattern="dd MM yyyy" value="${concert.date}" /> </td></tr>
        </c:forEach>
    </table>
    <a href="../bands">Back to Band List</a>
</head>
<body>

</body>
</html>
