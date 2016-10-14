package com.norisugosimasen.model.stationdata;

public enum PrefectureKey implements DataInterface {
    HOKKAIDO("北海道"),
    AOMORI("青森"),
    IWATE("岩手"),
    MIYAGI("宮城"),
    AKITA("秋田"),
    YAMAGATA("山形"),
    FUKUSHIMA("福島"),
    IBARAKI("茨城"),
    TOCHIGI("栃木"),
    GUNMA("群馬"),
    SAITAMA("埼玉"),
    CHIBA("千葉"),
    TOKYO("東京"),
    KANAGAWA("神奈川"),
    NIGATA("新潟"),
    TOYAMA("富山"),
    ISHIKAWA("石川"),
    FUKUI("福井"),
    YAMANASHI("山梨"),
    NAGANO("長野"),
    GIFU("岐阜"),
    SHIZUOKA("静岡"),
    AICHI("愛知"),
    MIE("三重"),
    SHIGA("滋賀"),
    KYOTO("京都"),
    OSAKA("大阪"),
    HYOGO("兵庫"),
    NARA("奈良"),
    WAKAYAMA("和歌山"),
    TOTTORI("鳥取"),
    SHIMANE("島根"),
    OKAYAMA("岡山"),
    HIROSHIMA("広島"),
    YAMAGUCHI("山口"),
    TOKUSHIMA("徳島"),
    KAGAWA("香川"),
    EHIME("愛媛"),
    KOCHI("高知"),
    FUKUOKA("福岡"),
    SAGA("佐賀"),
    NAGASAKI("長崎"),
    KUMAMOTO("熊本"),
    OITA("大分"),
    MIYAZAKI("宮崎"),
    KAGOSHIMA("鹿児島"),
    OKINAWA("沖縄"),;

    String mName;

    PrefectureKey(String name) {
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
    }

    public int getId() {
        return ordinal() + 1;
    }

    public static PrefectureKey indexOf(int index) {
        PrefectureKey result = PrefectureKey.HOKKAIDO;
        for (PrefectureKey prefecture : PrefectureKey.values()) {
            if (index == prefecture.ordinal()) {
                result = prefecture;
                break;
            }
        }

        return result;
    }
}
