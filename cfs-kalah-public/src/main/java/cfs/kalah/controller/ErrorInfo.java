package cfs.kalah.controller;

public class ErrorInfo {
    public final String url;
    public final String errorMessage;

    public ErrorInfo(String url, Exception ex) {
        this.url = url;
        this.errorMessage = ex.getLocalizedMessage();
    }
}
