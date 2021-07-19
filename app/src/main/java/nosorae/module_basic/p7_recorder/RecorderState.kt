package nosorae.module_basic.p7_recorder
// 상태에 따라 버튼 상태가 달라야해서 enum class 로 상태값을 지정해놓았다.
enum class RecorderState {
    BEFORE_RECORDING,
    ON_RECORDING,
    AFTER_RECORDING,
    ON_PLAYING
}