package com.hylamobile.demo.web

import com.hylamobile.demo.service.Counter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/counter")
class CounterController(val counter: Counter) {

    @GetMapping
    fun getCounter(): Int = counter.value()

    @PostMapping
    fun increment(): Int {
        counter.increment()
        return counter.value()
    }
}
