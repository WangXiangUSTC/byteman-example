package HelloWorld;

import java.util.*;
import java.time.*;

public class Info {
    LocalDateTime T;
    String Message;

    Info(LocalDateTime t, String Message) {
        this.T = t;
        this.Message = Message;
    }
    
    public void setTime(LocalDateTime t) {
        this.T = t;
    }

    public void setMessage(String msg) {
        this.Message = msg;
    }

    public String GetInfo() {
        return "date: " + this.T + ", message: " + this.Message;
    }
}
