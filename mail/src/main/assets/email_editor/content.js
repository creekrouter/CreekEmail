function content_handle() {
    let img_arr = document.getElementsByTagName("img");
    let path_list = '';
    let cid_list = '';
    for (let i = 0; i < img_arr.length; i++) {
        let img = img_arr[i];
        img.onclick = function () {
            window.jsAndroid.openImage(this.src);
        }
        if (img.src.startsWith('file:')) {
            path_list = path_list + img.getAttribute('src') + ';';
            cid_list = cid_list + img.getAttribute('custom_src') + ';';
        }
    }
//    console.log('lv123------:',path_list);
//    console.log('lv123------:',cid_list);
    window.jsAndroid.onHtmlChanged(document.getElementsByTagName('div')[0].innerHTML, path_list, cid_list);
}


document.body.addEventListener('DOMSubtreeModified', function () {
    content_handle();
});
