function heightClient(reduce){
    var parentHeight=document.getElementsByTagName("body")[0].clientHeight;
    document.getElementById("tableBox").style.height=parentHeight-120+"px";
}
heightClient();
