package notifier;

public enum NotificationEnum {

    Not9("9:00"),
    Not10("10:00"),
    Not11("11:00"),
    Not12("12:00"),
    Not13("13:00"),
    Not14("14:00"),
    Not15("15:00"),
    Not16("16:00"),
    Not17("17:00"),
    Not18("18:00");


    String time;

    NotificationEnum(String value) {
        this.time = value;
    }


}
