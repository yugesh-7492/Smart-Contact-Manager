console.log("this is script file");


const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {
        // Sidebar is visible — hide it
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%"); // fixed selector
    } else {
        // Sidebar is hidden — show it
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%"); // fixed selector
    }
};
