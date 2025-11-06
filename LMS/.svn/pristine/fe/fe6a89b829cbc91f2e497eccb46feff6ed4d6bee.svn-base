<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- D3.js 라이브러리 -->
<script src="https://d3js.org/d3.v7.min.js"></script>


<!-- Core JS -->
<!-- build:js assets/vendor/js/core.js -->
<script src="${pageContext.request.contextPath}/sneat-1.0.0/assets/vendor/libs/jquery/jquery.js"></script>
<script src="${pageContext.request.contextPath}/sneat-1.0.0/assets/vendor/libs/popper/popper.js"></script>
<script src="${pageContext.request.contextPath}/sneat-1.0.0/assets/vendor/js/bootstrap.js"></script>
<script src="${pageContext.request.contextPath}/sneat-1.0.0/assets/vendor/libs/perfect-scrollbar/perfect-scrollbar.js"></script>
<script src="${pageContext.request.contextPath}/sneat-1.0.0/assets/vendor/js/menu.js"></script>
<!-- endbuild -->

<!-- Vendors JS -->
<script src="${pageContext.request.contextPath}/sneat-1.0.0/assets/vendor/libs/apex-charts/apexcharts.js"></script>

<!-- Main JS -->
<script src="${pageContext.request.contextPath}/sneat-1.0.0/assets/js/main.js"></script>

<!-- Page JS -->
<script src="${pageContext.request.contextPath}/sneat-1.0.0/assets/js/dashboards-analytics.js"></script>

<!-- Place this tag in your head or just before your close body tag. -->
<script async defer src="https://buttons.github.io/buttons.js"></script>

<!-- SweetAlert2 JS -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<script>
    // Initialize CKEditor only if the element exists
    const ckEditorElement = document.querySelector('#ck-editor');
    if (ckEditorElement) {
        ClassicEditor
            .create(ckEditorElement)
            .catch(error => console.error('Editor error:', error));
    }
</script>

<!-- 학생 대시보드 차트 -->
<script src="${pageContext.request.contextPath}/js/app/student/dashboard/graduationChart.js"></script>
<script src="${pageContext.request.contextPath}/js/app/student/dashboard/semesterGradeChart.js"></script>
<script>
window.addEventListener('load', function() {
    // 현재 페이지가 학생 대시보드인 경우만 차트 렌더링
    const isStudentDashboard = document.body.classList.contains('student-dashboard')
                            || window.location.pathname.includes('/student');

    // 졸업 이수 차트
    if (document.getElementById('graduationChart') && isStudentDashboard) {
        const graduationChart = new GraduationChart('graduationChart');
        graduationChart.render();
    }

    // 전체 학기 성적 차트
    if (document.getElementById('gradeChart') && isStudentDashboard) {
        const gradeChart = new GradeChart('gradeChart');
        gradeChart.render();
    }
});
</script>


<%--  --%>


<!-- 메뉴 상태 유지 스크립트 -->
<!-- 메뉴 상태 항상 펼침 + 현재 페이지 active -->
<script>
$(function () {
  // 1) 모든 메뉴 펼침
  $('.menu-inner .menu-item').addClass('open');

  // 2) 현재 경로 기준 active 처리
  var contextPath = "${pageContext.request.contextPath}";
  var currentPath = window.location.pathname.replace(contextPath, '');

  var $currentLink = $('.menu-inner a[href="' + currentPath + '"]');

  if ($currentLink.length) {
    $currentLink.closest('.menu-item').addClass('active');
  }

  // 3) Perfect Scrollbar 자동 스크롤(선택)
  if (typeof PerfectScrollbar !== 'undefined') {
    var menuInner = document.querySelector('.menu-inner');
    if (menuInner) {
      var ps = new PerfectScrollbar(menuInner);
      var $activeItem = $('.menu-item.active:last');
      if ($activeItem.length) {
        menuInner.scrollTop =
          $activeItem.offset().top - $(menuInner).offset().top - 100;
        ps.update();
      }
    }
  }
});
</script>

<!-- //     // 1. 현재 페이지의 경로 -->
<%-- //     var contextPath = "${pageContext.request.contextPath}"; --%>
<!-- //     var currentPath = window.location.pathname.replace(contextPath, ''); -->

<!-- //     // 2. 모든 메뉴 항목의 'active'와 'open' 상태를 초기화 -->
<!-- //     $('.menu-inner .menu-item').removeClass('active open'); -->

<!-- //     // 3. 현재 경로와 href 속성이 일치하는 링크 찾기 -->
<!-- //     var $currentLink = $('.menu-inner a[href="' + currentPath + '"]'); -->

<!-- //     if ($currentLink.length > 0) { -->

<!-- //         // 4. 해당 링크의 메뉴 아이템(<li>)에 'active' 클래스를 추가 -->
<!-- //         $currentLink.closest('.menu-item').addClass('active'); -->

<!-- //         // 5. 상위 메뉴를 찾아 'open' 클래스를 추가하여 펼쳐진 상태를 유지 -->
<!-- //         var $parentMenuSub = $currentLink.closest('.menu-sub'); -->

<!-- //         if ($parentMenuSub.length > 0) { -->
<!-- //             // 상위 li.menu-item에 'open' 클래스 추가 -->
<!-- //             $parentMenuSub.closest('.menu-item').addClass('open'); -->

<!-- //             // 다단계 메뉴 처리 -->
<!-- //             $parentMenuSub.parents('.menu-sub').each(function() { -->
<!-- //                 $(this).closest('.menu-item').addClass('open'); -->
<!-- //             }); -->
<!-- //         } -->

<!-- //         // 6. Perfect Scrollbar 업데이트 -->
<!-- //         if (typeof PerfectScrollbar !== 'undefined') { -->
<!-- //             const menuInner = document.querySelector('.menu-inner'); -->
<!-- //             if (menuInner) { -->
<!-- //                 const ps = new PerfectScrollbar(menuInner); -->
<!-- //                 const $activeItem = $('.menu-item.active:last'); -->
<!-- //                 if ($activeItem.length) { -->
<!-- //                     menuInner.scrollTop = $activeItem.offset().top - $(menuInner).offset().top - 100; -->
<!-- //                     ps.update(); -->
<!-- //                 } -->
<!-- //             } -->
<!-- //         } -->
<!-- //     } -->
<!-- // }); -->

