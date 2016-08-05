# Ego


Compile time generated JavaORM for MongoDB — Edit

#### **Currently under development!!!**

Operations supported:

* Create
* Read (quite simple, though)

## Overview

**Ego** analyzes your models and generates classes for DB operations

---

## Roadmap

###Must have:

- Builder generation (**Done**)
- Codec generation (Half done, working on it...)
- Create class generation (**Done**)
- Read class generation (**Done**, very simple though, it will be improved with filter and sort operations)
- Delete class generation
- Filter class generation
- Patch class generation
- Update class generation

###Nice to have:

- Specify the collection name in the constructor


> I'm open to suggestions so feel free to open an issue at https://github.com/AlbertoSH/Ego/issues

---

## How to start

1. Clone this repo
  * `git clone https://github.com/AlbertoSH/Ego.git`
2. Make your objects extend `EgoObject`
  * You have an example at `com.github.albertosh.ego.sample.SimpleItem`
3. Compile sample (or your code) with Gradle
  * `./gradlew ego-sample:compileJava`
4. The code generated is in `./ego-sample/build/generated/source/apt`
5. Create a `CodecRegistry` with all your codecs (**NOTE: This step will be done automatically in future versions**)
   
        List<Codec<?>> codecs = new ArrayList<>();
        codecs.add(new SimpleItemEgoCodec());
        ... // all your codecs
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(codecs));


6. Instantiate a `MongoClient` including the `CodecRegistry`

        MongoClientOptions options = MongoClientOptions.builder()
                .codecRegistry(codecRegistry)
                .build();
        MongoClient client = new MongoClient("localhost:27017", options)

7. You can now create your I*EgoCreate, I*EgoRead classes with that `MongoClient` and a database name:
  * I recommend you to declare your variables as interfaces so you can change the implementation whenever you want 

                ISimpleItemEgoCreate create = new SimpleItemEgoCreate(client, "ego");
                ISimpleItemEgoRead read = new SimpleItemEgoRead(client, "ego");


**You have a use example at `test` folder**




---

##License

    The MIT License

    Copyright (c) 2016 Alberto Sanz

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
