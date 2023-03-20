package com.creek.common;

import java.io.Serializable;


public class MailContact implements Serializable {

    private String user_email = "";
    private String last_time = "";
    private String display_name = "";
    private String email_addr = "";

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    private String department = "";

    public MailContact() {
    }

    public MailContact(String user_email, String display_name, String email_addr) {
        this.user_email = user_email;
        this.display_name = display_name;
        this.email_addr = email_addr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MailContact that = (MailContact) o;

        if (!user_email.equals(that.user_email)) return false;
        return email_addr.equals(that.email_addr);

    }

    @Override
    public int hashCode() {
        int result;
        if (user_email != null){
            result = user_email.hashCode();
        }else {
            result = "".hashCode();
        }
        if (email_addr != null){
            result = 31 * result + email_addr.hashCode();
        }else {
            result = 31 * result + "".hashCode();
        }
        return result;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getEmail_addr() {
        return email_addr;
    }

    public void setEmail_addr(String email_addr) {
        this.email_addr = email_addr;
    }
}
