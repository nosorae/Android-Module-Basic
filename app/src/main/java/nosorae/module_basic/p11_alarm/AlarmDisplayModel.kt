package nosorae.module_basic.p11_alarm

data class AlarmDisplayModel(
    val hour: Int, // 0~23 의 값이 들어온다.
    val minute: Int,
    var onOff: Boolean // val 이 아니라 var 임에 주의
) {
    // getter 함수 만드는 법
    val timeText: String
        get() {
            // 두자리 수에 앞은 0으로 채워라 라는 의미의 format 인 %02d
            val h = "%02d".format(if (hour < 12) hour else hour - 12)
            val m = "%02d".format(minute%60)
            return "$h:$m"
        }

    val amPmText: String
        get() {
            return if (hour < 12) "AM" else "PM"
        }

    val onOffText: String
        get() {
            // 꺼져 있는 상태에서 버튼을 눌렀다면 알람 켜기, 켜져 있는 상태에서 눌렀다면 알람 끄기
            return if (onOff) "알람 끄기" else "알람 켜기"
        }

    // 이렇게 객체를 String 화 해서 SharedPreference 에 저장할 수도 있다.
    fun makeDataForDB(): String {
        return "$hour:$minute"
    }
}
