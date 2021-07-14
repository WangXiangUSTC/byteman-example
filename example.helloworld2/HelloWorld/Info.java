package HelloWorld;

import java.util.*;
import java.time.LocalDateTime;

public class Info {
    private int num;
    private String message;
    private LocalDateTime time;

    public Info(int num, LocalDateTime time, String msg) {
        this.num = num;
        this.message = msg;
        this.time = time;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String Str() {
        return this.num + ". " + this.time + " " + this.message;
    }
}
