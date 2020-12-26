# Bricks
###### A modular synthesizer library


## Description
This library imitates modules and patches used to create a modular synthesizer. 


## Features (for now)
* Oscillators: sine, triangle, square, saw up, saw down, noise.
* Filters: low-pass, high-pass, band-pass, delay.
* LFO
* Utilities: mixer, passthrough.


## Note
*WIP*


## Basics
Combine the various modules on the [builtin](https://github.com/jbatistareis/bricks/tree/master/src/main/java/com/jbatista/bricks/components/builtin) package, connecting then [patches](https://github.com/jbatistareis/bricks/blob/master/src/main/java/com/jbatista/bricks/components/Patch.java), to build a synthesizer.


## Example  
See [VSynth 1](https://github.com/jbatistareis/vsynth1) for a fairly complex use case on the [InstrumentBoard](https://github.com/jbatistareis/vsynth1/blob/master/core/src/com/jbatista/vsynth/components/modules/InstrumentBoard.java) class.  
```java
// TODO
```


## TODO
- [ ] Proper README/documentation
- [ ] Channel separation/manipulation
- [ ] Parameters serialization
- [ ] Parameters fine-tuning
- [ ] API fine-tuning
- [ ] MIDI support  
*And a lot more*