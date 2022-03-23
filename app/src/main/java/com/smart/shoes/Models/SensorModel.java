package com.smart.shoes.Models;

public class SensorModel {
   String speed;
   String distance;
   String emgSignal;
   String time;
   String date;

   public SensorModel() {
   }

   public SensorModel(String speed, String distance, String emgSignal, String time, String date) {
      this.speed = speed;
      this.distance = distance;
      this.emgSignal = emgSignal;
      this.time = time;
      this.date = date;
   }

   public String getSpeed() {
      return speed;
   }

   public void setSpeed(String speed) {
      this.speed = speed;
   }

   public String getDistance() {
      return distance;
   }

   public void setDistance(String distance) {
      this.distance = distance;
   }

   public String getEmgSignal() {
      return emgSignal;
   }

   public void setEmgSignal(String emgSignal) {
      this.emgSignal = emgSignal;
   }

   public String getTime() {
      return time;
   }

   public void setTime(String time) {
      this.time = time;
   }

   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }
}
