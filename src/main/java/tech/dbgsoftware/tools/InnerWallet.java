package tech.dbgsoftware.tools;

public class InnerWallet {
  private String sed;

  private String address;

  public InnerWallet() {
  }

  public InnerWallet(String sed, String address) {
    this.sed = sed;
    this.address = address;
  }

  public String getSed() {
    return sed;
  }

  public void setSed(String sed) {
    this.sed = sed;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public String toString() {
    return "InnerWallet{" +
        "sed='" + sed + '\'' +
        ", address='" + address + '\'' +
        '}';
  }
}
