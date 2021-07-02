package threadPetHospital;


public class Pet {
    private String name;
    private String kind;
    private String weight;
    private String type;
    private String oname;
    private String otelephone;
    private String birth;
    private String time;

//  Pet(String pname, String pkind, String pweight, String ptype, String poname, String potele, String pbirth, String ptime){}


    public void setName(String name) {
        this.name = name;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOname(String oname) {
        this.oname = oname;
    }

    public void setOtelephone(String otelephone) {
        this.otelephone = otelephone;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOtelephone() {
        return otelephone;
    }

    public String getWeight() {
        return weight;
    }

    public String getOname() {
        return oname;
    }

    public String getBirth() {
        return birth;
    }

    public String getType() {
        return type;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

}
