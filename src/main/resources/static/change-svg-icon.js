document.addEventListener('DOMContentLoaded', function() {
    var svgImage = document.getElementById('trash-bin');

    svgImage.addEventListener('mouseover', function() {
        svgImage.src = '/static/trash-bin-open.svg';
    });

    svgImage.addEventListener('mouseout', function() {
        svgImage.src = '/static/trash-bin-closed.svg';
    });
});