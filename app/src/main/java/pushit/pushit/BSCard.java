package pushit.pushit;

/**
 * Created by gddjr on 2016-12-09.
 */

public class BSCard {

    private String img_url;
    private String phone_number;

    public BSCard(String img_url, String phone_number) {

        this.img_url = img_url;
        this.phone_number = phone_number;
    }

    public String getImgUrl() {

        return img_url;
    }

    public String getPhoneNumber() {

        return phone_number;
    }
}
