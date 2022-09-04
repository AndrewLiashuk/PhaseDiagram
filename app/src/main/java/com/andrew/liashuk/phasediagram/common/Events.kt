package com.andrew.liashuk.phasediagram.common

abstract class Event

class ShowProgress : Event()

class HideProgress : Event()

class ShowToast(val message: String) : Event()