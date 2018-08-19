package kr.okky.app.android.global

enum class DrawerMenu constructor(private val path:String, private val menuName:String) {
    QNA("/articles/questions", "Q&A"),
    QNA_TECH("/articles/tech-qna", "Tech Q&A"),
    QNA_BLOCK_CHAIN("/articles/blockchain-qna", "Blockchain Q&A"),

    TECH("/articles/tech", "Tech"),
    TECH_IT_NEWS("/articles/news", "IT News & 정보"),
    TECH_TIPS("/articles/tips", "Tips & 강좌"),

    COMMUNITY("/articles/community", "커뮤니티"),
    COMMUNITY_NOTICE("/articles/notice", "공지사항"),
    COMMUNITY_LIFE("/articles/life", "사는얘기"),
    COMMUNITY_FORUM("/articles/forum", "포럼"),
    COMMUNITY_EVENT("/articles/event", "IT행사"),
    COMMUNITY_GATHERING("/articles/gathering", "정기모임/스터디"),
    COMMUNITY_PROMOTE("/articles/promote", "학원홍보"),

    COLUMNS("/articles/columns", "컬럼"),

    JOBS("/articles/jobs", "Jobs"),
    JOBS_EVALCOM("/articles/evalcom", "좋은회사/나쁜회사"),
    JOBS_RECRUIT("/articles/recruit", "구인"),
    JOBS_RESUMES("/articles/resumes", "구직"),
    JOBS_EBRAIN_JOBS("/user/info/35324", "eBrainJobs"),
    JOBS_RESUMES_FULLTIME("/articles/recruit?filter.jobType=FULLTIME", "구인[정규직]"),

    CONTACT("info@okky.kr", "Contact"),
    BUG_REPORT("https://github.com/okjsp/okky/issues", "Github Issues"),
    SOURCE("https://github.com/neoburi/okky-android.git", "Source");

    fun path():String = path
    fun menuName():String = menuName
}