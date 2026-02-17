
package com.cts.dto;

public class ClaimRequest {
String note;
String activationcode;
int points;
public ClaimRequest() {
    super();

}
public ClaimRequest(String note, String activationcode, int points) {
    super();
    this.note = note;
    this.activationcode = activationcode;
    this.points = points;
}

public String getNote() {
    return note;
}   
public void setNote(String note) {
    this.note = note;
}

public String getActivationcode() {
    return activationcode;
}
public void setActivationcode(String activationcode) {
    this.activationcode = activationcode;
}
public int getPoints() {
    return points;
}
public void setPoints(int points) {
    this.points = points;
}

}
