<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${not empty sessionScope.flash_success}">
    <div class="alert alert-success alert-dismissible flash-banner">
        <i class="bi bi-check-circle-fill me-2"></i><c:out value="${sessionScope.flash_success}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="flash_success" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flash_error}">
    <div class="alert alert-danger alert-dismissible flash-banner">
        <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${sessionScope.flash_error}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="flash_error" scope="session"/>
</c:if>
