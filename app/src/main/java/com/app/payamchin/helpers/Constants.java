package com.app.payamchin.helpers;

public class Constants {

    // Bank sms keywords
    public static final String[] selected_keywords = new String[] {"انتقال","تراکنش","مانده","واریز","برداشت","موجودی","بانک","مبلغ","کارت"};

    // Banned sms address
    public static final String[] banned_address = new String[] {"HAMRAHAVAL","IRANCELL",".IRANCELL."};

    // Ad keywords
    public static final String[] ad_keywords = new String[] {"فروش","ویژه","تخفیف","حراج","قرعه کشی","خرید","جشنواره"};

    // SSL certificate (gateway)
    public static final String ssl_certificate = "Issued to;\n" +
            "CN=bpm_shaparaKir,O=SHAPARAK,OU=1T" +
            "DEPARTMENT,L:TEHRAN,ST:TEHRAN,C:\\R," +
            "2.5.4,5=#1306343230303133,2.5.4.15=#137450" +
            "726976617465204f7267616e69736174696f6e," +
            "1.3,6,1.4‘1.3W1,60.2.W,3=#13024952, Issued" +
            "by: C EYNECTIS Extended Validation" +
            "CA,OU:Entity of KEYNECTIS for CA" +
            "services,O=Cenplus,C=FR;";

    // Please activate message
    public static final String PLEASE_ACTIVATE = "پیامچین به مدت یکسال از حریم خصوصی و امنیت اطلاعات شما محافظت خواهد کرد. آیا مایلید هم اکنون برنامه را فعالسازی نمایید؟";

    // Permission request code
    public static final int REQUEST_CODE = 1;

    // Permission requesting message
    public static final String PERMISSION_MESSAGE = "کاربر گرامی جهت ادامه فعالیت لطفا مجوز های لازم را صادر نمایید.";

    // Server error message
    public static final String SERVER_ERROR = "خطا در برقراری ارتباط";

    // Progress dialog waiting message
    public static final String PLEASE_WAIT = "لطفا کمی صبر کنید ... ";

    // Progress dialog hide delay
    public static final long DIALOG_HIDE_DELAY = 2000;

    // Splash screen time
    public static final long SPLASH_SCREEN_DELAY = 3000;

    // Static source to fetch IP
    public static final String IP_SOURCE_URL = "https://res.cloudinary.com/dkpk9dbw7/raw/upload/v1532005856/payamchin/ip.txt" ;

    // Connecting to bazaar dialog message
    public static final CharSequence CONNECTING_BAZAAR = "در حال ارتباط با کافه بازار" ;

    public static final String VAS_CLEAN_MESSAGE = "تمامی سرویس های ارزش افزوده برای شما غیر فعال هستند.";

    public static final String VIRUS_CLEAN_MESSAGE = "دستگاه شما در امنیت کامل است.";

    public static final String AD_CLEAN_MESSAGE = "پیامک های تبلیغاتی مسدود هستند.";

    public static final String NETWORK_ERROR = "دسترسی به اینترنت وجود ندارد";

    public static final String PERMISSION_FAILED = "مجوز توسط کاربر لغو شد";

    public static final String WELCOME_MESSAGE = "خوش آمدید";

    public static final String PAYMENT_SUCCESSFULL = "پرداخت با موفقیت انجام شد.";

    public static final String PAYMENT_FAILED = "پرداخت ناموفق بود." ;

    public static final String UNKNOWN_ERROR = "خطای نامشخص";
}
