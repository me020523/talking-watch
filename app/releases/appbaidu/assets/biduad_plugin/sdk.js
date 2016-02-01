/**
 * @file protocol between android splash and template
*/
(function () {
    window.baidu = {};
    window.baidu.mobads = {};

    window.baidu.mobads.Sdk = {
        isIOS: false
    };
    var Sdk = window.baidu.mobads.Sdk;
    Sdk.isIOS = (/iphone|ipad|ipod/i).test(window.navigator.userAgent.toLowerCase());

    var mob = window.baidu.mobads;
    mob.Act = {
        LP: 1,
        DL: 2,
        MAP: 4,
        SMS: 8,
        MAIL: 16,
        PHONE: 32,
        VIDEO: 64,
        RM: 128,
        NA: 256,
        APO: 512
    };
    var win = window;
    win.MobadsSdk = win.MobadsSdk || {};
    var MobadsSdk = win.MobadsSdk;

    var send3rdLog = function (isShowLog, ad) {
        if (!ad || !ad.mon) {
            return;
        }

        var url;
        for (var i = 0; i < ad.mon.length; ++i) {
            url = isShowLog ? ad.mon[i].s : ad.mon[i].c;
            if (!url) {
                continue;
            }
            new Image().src = url;
        }
    };


    Sdk.setActionUrl = function (url, inapp, act, title, close) {
        var opt = {};
        if ('[object Object]' === Object.prototype.toString.call(url)) {
            opt = url;

            url = opt.url;
            inapp = opt.inapp;
            act = opt.act;
            title = opt.title;
            close = opt.close;
        }
        opt.url = url || '';
        opt.inapp = inapp || false;
        opt.act = act || 1;
        opt.title = title || '';
        opt.close = close || false;

        opt.logurl = opt.logurl || '';
        opt.weibo = opt.weibo || '';
        opt.map = opt.map || '';
        opt.search = opt.search || '';
        opt.sms = opt.sms || '';
        opt.at = opt.at || 1;

        opt.tid = opt.tid || '';

        if (MobadsSdk.setActionUrl) {
            var DUMP_PAR = opt.inapp;
            MobadsSdk.setActionUrl(JSON.stringify(opt), DUMP_PAR);
        }

    };
    Sdk.sendClickLog = function (logurl) {
        new Image().src = logurl;
    };

    Sdk.onAdPlayEnd = function () {
        if (MobadsSdk.onAdPlayEnd) {
            setTimeout(function () {
                    MobadsSdk.onAdPlayEnd();
                },
                300);
        }
    };
    Sdk.open = function (url, options) {
        var option = {
            url: url,
            inapp: true,
            act: mob.Act.LP
        };
        Sdk.setActionUrl(option);
        send3rdLog(false, options);
    };


    Sdk.startDownload = function (url, options) {
        var ad = {};
        ad = options || {};
        ad.tit = options && options.tit || '应用';
        var mobadsJumpUrl = url;
        if (/^itms-services:\/\//.test(url)) {
            Sdk.setActionUrl(url, false, mob.Act.DL, ad.tit, true);
            return;
        }

        if (Sdk.isIOS) {
            var tid = options && options.pinfo && options.pinfo.tid;
            if (tid) {
                Sdk.sendClickLog(mobadsJumpUrl);
            }
            Sdk.setActionUrl({
                url: url,
                tid: tid || '',
                inapp: true,
                act: mob.Act.DL
            });
            return;
        }
        var mon = options && options.mon || [];
        var id = options && options.id || 1;
        var pk = options && options.pk || '';
        var qk = options && options.qk || '';
        var exp2 = options && options.exp2 || {};
        var wi = options && options.wi ? true : false;
        var title = ad.tit;

        Sdk.setActionUrl({
            url: mobadsJumpUrl,
            act: mob.Act.DL,
            close: true,
            adid: id,
            originUrl: mobadsJumpUrl,
            dlTunnel: 3,
            autoOpen: true,
            popNotif: true,
            canCancel: true,
            canDelete: 5,
            mon: mon,
            pk: pk,
            qk: qk,
            adid: id,
            title: ad.tit
        });

        send3rdLog(false, options);
    };

    Sdk.handleClick = function (options) {
        var ad = options || {};
        var Act = mob.Act;
        if (Act.LP === ad.act) {
            Sdk.open(ad.curl, ad);
        } else if (Act.DL === ad.act) {
            Sdk.startDownload(ad.curl, ad);
        }
    };
})();
