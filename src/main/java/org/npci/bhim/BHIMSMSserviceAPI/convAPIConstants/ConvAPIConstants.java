package org.npci.bhim.BHIMSMSserviceAPI.convAPIConstants;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
/** 1.text-->pre_05092025
 2.Rich card --> pre_image_05092025
 3.carousal--> pre_bbps_05092025
 4.bbps_pre_04092025
 5.pre_bbps_01092025*/
@Getter
@NoArgsConstructor
public class ConvAPIConstants {


    public static final String Conv_app_id="01K3TK81HNF380VBF1E58GBWRK";
    @Getter
    public class WATempltes{
        public static final String p2p_cohort_19082025="p2p_cohort_19082025";
        public static final String bbps_pre_06082025="bbps_pre_06082025";
        public static final String WBAID="655084867517929";
    }
    @Getter
    public  class RCSTemplates{
        public static final String text_pre_05092025="pre_05092025";
        public static final String Rich_card_pre_image_05092025="pre_image_05092025";
        public static final String carousal_pre_bbps_05092025="pre_bbps_05092025";
        public static final String  RCS_bbps_pre_04092025="bbps_pre_04092025";
        public static final String RCS_pre_bbps_01092025="pre_bbps_01092025";
    }

}
