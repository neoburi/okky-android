package kr.okky.app.android.global

@Deprecated(
        message = "discard 2018.08.26"
)
enum class DrawerMenu constructor(
        private val path:String, private val menuName:String, private val active:Boolean) {
    QNA("/articles/questions", "Q&A", true),
    QNA_TECH("/articles/tech-qna", "Tech Q&A", true),
    QNA_BLOCK_CHAIN("/articles/blockchain-qna", "Blockchain Q&A", true),

    TECH("/articles/tech", "Tech", true),
    TECH_IT_NEWS("/articles/news", "IT News & 정보", true),
    TECH_TIPS("/articles/tips", "Tips & 강좌", true),

    COMMUNITY("/articles/community", "커뮤니티", true),
    COMMUNITY_NOTICE("/articles/notice", "공지사항", true),
    COMMUNITY_LIFE("/articles/life", "사는얘기", true),
    COMMUNITY_FORUM("/articles/forum", "포럼", true),
    COMMUNITY_EVENT("/articles/event", "IT행사", true),
    COMMUNITY_GATHERING("/articles/gathering", "정기모임/스터디", true),
    COMMUNITY_PROMOTE("/articles/promote", "학원홍보", false),

    COLUMNS("/articles/columns", "컬럼", true),

    JOBS("/articles/jobs", "Jobs", true),
    JOBS_EVALCOM("/articles/evalcom", "좋은회사/나쁜회사", true),
    JOBS_RECRUIT("/articles/recruit", "구인", true),
    JOBS_RESUMES("/articles/resumes", "구직", false),
    JOBS_EBRAIN_JOBS("/user/info/35324", "eBrainJobs", false),
    JOBS_RESUMES_FULLTIME("/articles/recruit?filter.jobType=FULLTIME", "구인[정규직]", true),

    CONTACT("info@okky.kr", "Contact", true),
    BUG_REPORT("https://github.com/okjsp/okky/issues", "Github Issues", false),
    SOURCE("https://github.com/neoburi/okky-android.git", "Source", false);

    fun path():String = path
    fun menuName():String = menuName
    fun isActive():Boolean = active
}