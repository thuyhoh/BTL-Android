package com.example.myapplication;

public class toAND {
    public int Temp;
    public int Humi;
    public int Time;
    public boolean Fans;

    public toAND(){
        this.Temp = 0;
        this.Humi = 0;
        this.Time = 0;
        this.Fans = true;
    }

    public toAND(int temp, int humi, int time, boolean fans){
        this.Temp = temp;
        this.Humi = humi;
        this.Time = time;
        this.Fans = fans;
    }

}
