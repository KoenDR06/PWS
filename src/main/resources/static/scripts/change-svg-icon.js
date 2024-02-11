function changeSvgIcon(checkboxId, svgId) {
    // let checkbox = document.getElementById(checkboxId);
    let svg = document.getElementById(`svg-icon-${svgId}`);

    // console.log(checkboxId)

    // checkbox.checked = !checkbox.checked

    if (svg.src.includes("/static/images/refresh-recipe.svg")) {
        svg.src = "/static/images/chefs-hat.svg"
    } else if (svg.src.includes("/static/images/chefs-hat.svg")) {
        svg.src = "/static/images/refresh-recipe.svg"
    } else {
        console.log(svg.src)
    }
}