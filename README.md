## Purpose
- Reduce repeatitive code by handle cases in Base Clasess
- Easier way to get and observe data
--------
## OverView
Pass to BaseViewModel a list of empty (nullValue) ViewStates of each valueType a call can return. <br />
It'll handle to save it in state by itself. <br /> 
```kotlin
class MainVM(private val someRepository: SomeRepository) :
    BaseViewModel(
        states = mutableListOf(
            ViewState<FirstModel>(),
            ViewState<SecondModel>()
        )
    ) {
```

- launch() -> Launchs a coroutine in viewModelScope and handles some other staff too. <br /> 
- call() -> Makes a requestCall and saves data into corresponding state (from states that we've passed to a BaseViewModel) by index. <br />
```kotlin
private fun getBothData() {
        launch {
            call(someRepository.getFirstTestData(), 0)
            call(someRepository.getSecondTestData(), 1)
        }
    }
```
- collect() -> Collects data with corresponding index and Type. <br /> 
- collectNullable -> Collects Nullable data with corresponding index and Type. <br /> 
```kotlin
 override fun setupObservers() {
        collect<FirstModel>(0) {
            binding.textviewFirst.text = firstData
        }
        collectNullable<SecondModel>(1) {
            binding.tvSecond.text = this?.secondData.orEmpty()
        }
    }
```
---------
## For more details, see: 
<details>
  <summary>Table of Contents</summary>
  <ul>
    <li><a href="#purpose">Purpose</a></li>
    <li><a href="#overview">OverView</a></li>
  </ul>
</details>
