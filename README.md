Spring Boot Application with Clojure REPL
=========================================

This project is to demonstrate the potential of REPL embedded into
running Spring application.

Installation
------------

You need [leiningen](https://leiningen.org/) to be installed. I use
[SDKMAN!](https://sdkman.io/):

```bash
sdk install leiningen
```

Download the demo application:

```bash
git clone https://github.com/dmitry-at-hyla/spring-clojure-repl-demo.git
```

Run
---

Run the application:

```bash
./gradlew bootRun
```

The application represents a simple counter with two endpoints:

* **GET /counter** return the current value of the counter
* **POST /counter** increment the counter by one

Let's try it:

```bash
> curl http://localhost:8080/counter
0

> curl -X POST http://localhost:8080/counter
> curl -X POST http://localhost:8080/counter


> curl http://localhost:8080/counter
2
```

That's simple.

REPL: connect
-------------

To connect to the REPL use leiningen:

```bash
lein :connect localhost:1111
```

And import some useful namespaces:

```clojure
(use 'cl-java-introspector.spring)
(use 'cl-java-introspector.core)
```

Let's see what use cases we could have.

REPL: gather information
------------------------

First of all, we are able to gather the information about beans in the 
Spring context:

```clojure
(get-beans)
```

or find a specific bean:

```clojure
> (def simple-counter (get-bean "simpleCounter"))
#'user/simple-counter
```

Please review [SimpleCounter](src/main/kotlin/com/hylamobile/demo/service/SimpleCounter.kt)
before reading next sections.

REPL: change the state
----------------------

Let's check one more time what is the current value of the counter:

```bash
> curl http://localhost:8080/counter
2
```

That's fine. We could have access it from the REPL as well:

```clojure
> (.value simple-counter)
2
```

We can call the `increment` method directly:

```clojure
> (.increment simple-counter)
nil
> (.value simple-counter)
3
```

Yes, it's changed:

```bash
> curl http://localhost:8080/counter
3
```

Let's try to reset it using Reflection API:

```clojure
> (def holder (.getDeclaredField (class simple-counter) "holder"))
#'user/holder
> (. holder (setAccessible true))
nil
> (. holder (get simple-counter))
3
user=> (. holder (set simple-counter (int 0)))
nil
> (. holder (get simple-counter))
0
```

Nice! Let's check:

```bash
> curl http://localhost:8080/counter
0
```

REPL: substitute the bean
-------------------------

Let's have more fun! What if we try to substitute one implementation
bean with the another? Let's try to create a bean definition for a
"constant counter", the counter that never changes:

```clojure
(import com.hylamobile.demo.service.Counter)

(def constant-counter
    (reify Counter
        (value [this] 42)
        (increment [this])))

(import org.springframework.beans.factory.support.RootBeanDefinition)
(import java.util.function.Supplier)

(def bean-def
    (new RootBeanDefinition
        (class constant-counter)
        (reify Supplier
            (get [this] constant-counter))))
```

OK, now we need to get application context registry:

```clojure
(import '(net.matlux NreplServerSpring))

(def app-ctx (.getApplicationContext NreplServerSpring/instance))
(def registry (.getAutowireCapableBeanFactory app-ctx))
```

Now we can register a new bean, and remove an old one:

```clojure
(.registerBeanDefinition registry "constantCounter" bean-def)
(.removeBeanDefinition registry "simpleCounter")
```

Nice! Let's test it:

```bash
> curl http://localhost:8080/counter
42
```

That's all folks!
