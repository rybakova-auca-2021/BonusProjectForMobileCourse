package com.example.bonusproject.api

data class ApiResponse(val results: Results)

data class Results(val A: A)

data class A(val status: Int, val frames: List<Frame>)

data class Frame(val schema: Schema, val data: Data)

data class Schema(val name: String)

data class Data(val values: List<List<Any>>)

