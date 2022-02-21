package entidades;

public class Driver  {
    private String idDriver;
    private String uidUser;
    private String dni;
    private String licence;
    private String dateExpLicencia;
    private String urlLicence;
    private String urlServices;
    private String urlDocPolice;

    private String adress;
    private String dateReg;
    private String descriptionDriver;

    private String uidCreditCard;
    private String uidScore;
    private String uidVehicle;

    public String getUidUser() {
        return uidUser;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setUidUser(String uidUser) {
        this.uidUser = uidUser;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getDateExpLicencia() {
        return dateExpLicencia;
    }

    public void setDateExpLicencia(String dateExpLicencia) {
        this.dateExpLicencia = dateExpLicencia;
    }

    public String getUrlLicence() {
        return urlLicence;
    }

    public void setUrlLicence(String urlLicence) {
        this.urlLicence = urlLicence;
    }

    public String getUrlServices() {
        return urlServices;
    }

    public void setUrlServices(String urlServices) {
        this.urlServices = urlServices;
    }

    public String getUrlDocPolice() {
        return urlDocPolice;
    }

    public void setUrlDocPolice(String urlDocPolice) {
        this.urlDocPolice = urlDocPolice;
    }


    public String getDateReg() {
        return dateReg;
    }

    public void setDateReg(String dateReg) {
        this.dateReg = dateReg;
    }

    public String getDescriptionDriver() {
        return descriptionDriver;
    }

    public void setDescriptionDriver(String descriptionDriver) {
        this.descriptionDriver = descriptionDriver;
    }

    public String getUidCreditCard() {
        return uidCreditCard;
    }

    public void setUidCreditCard(String uidCreditCard) {
        this.uidCreditCard = uidCreditCard;
    }

    public String getUidScore() {
        return uidScore;
    }

    public void setUidScore(String uidScore) {
        this.uidScore = uidScore;
    }

    public String getUidVehicle() {
        return uidVehicle;
    }

    public void setUidVehicle(String uidVehicle) {
        this.uidVehicle = uidVehicle;
    }
}
