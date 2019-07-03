//初始化的时候，地区列表获取与更新

var before = {
    getTowns: function () {
        var me = this;//绑定对象
        $.ajax({
            type: 'POST',
            url: '/old/town/list',
            async:false,
            success: function (data) {
                var townStr = '';
                for (var i = 0; i < data.length; i++) {
                    if (i == 0) {
                        townStr += "<option value='" + data[i].BH + "' selected>" + data[i].MC + "</option>";
                    } else {
                        townStr += "<option value='" + data[i].BH + "'>" + data[i].MC + "</option>";
                    }
                }
                $(".towns").html(townStr);
                me.getVillages($(".towns").eq(0).val());
            }
        })
    },
    getVillages: function (citiCode,filter) {
        $.ajax({
            type: 'POST',
            url: '/old/village/list',
            data: {
                town: citiCode
            },
            async:false,
            success: function (data) {
                var villageStr = '';
                for (var i = 0; i < data.length; i++) {
                    if (i == 0) {
                        villageStr += "<option value='" + data[i].BH + "' selected>" + data[i].MC + "</option>";
                    } else {
                        villageStr += "<option value='" + data[i].BH + "'>" + data[i].MC + "</option>";
                    }
                }
                if(filter){
                    $("."+filter).html(villageStr);
                }else{
                    $(".village").html(villageStr);
                }
            }
        })
    }
}
before.getTowns();