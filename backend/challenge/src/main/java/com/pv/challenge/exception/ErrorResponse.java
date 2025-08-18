package com.pv.challenge.exception;

public class ErrorResponse {
  private int status;
  private String error;
  private String message;
  private String path;
  private String timestamp;

  public ErrorResponse() {}

  public ErrorResponse(int status, String error, String message, String path, String timestamp) {
    this.status = status; this.error = error; this.message = message; this.path = path; this.timestamp = timestamp;
  }

  public int getStatus() { return status; }
  public String getError() { return error; }
  public String getMessage() { return message; }
  public String getPath() { return path; }
  public String getTimestamp() { return timestamp; }

  public void setStatus(int status) { this.status = status; }
  public void setError(String error) { this.error = error; }
  public void setMessage(String message) { this.message = message; }
  public void setPath(String path) { this.path = path; }
  public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
