# hackerdict

#### The community driven dictionary of hacker interests.

---

A [Heroku](http://www.heroku.com) web app using Compojure.

This project has a few basics set up beyond the bare Compojure defaults:

* Cookie-backed session store
* Stack traces when in development
* Environment-based config via [environ](https://github.com/weavejester/environ)
* [HTTP-based REPL debugging](https://devcenter.heroku.com/articles/debugging-clojure) via [drawbridge](https://github.com/cemerick/drawbridge)





## Usage

To start a local web server for development you can either eval the
commented out forms at the bottom of `web.clj` from your editor or
launch from the command line:

    $ lein run

You'll need the [heroku toolbelt](https://toolbelt.herokuapp.com)
installed to manage the heroku side of your app. Once it's installed,
get the app created:

    $ heroku apps:create hackerdict
    Creating hackerdict... done, stack is cedar
    http://hackerdict.herokuapp.com/ | git@heroku.com:hackerdict.git
    Git remote heroku added

Set Datomic credentials:

    heroku config:add BUILD_CONFIG_WHITELIST="DATOMIC_USERNAME DATOMIC_PASSWORD"
    heroku config:add DATOMIC_USERNAME="email@provider.com"
    heroku config:add DATOMIC_PASSWORD="pass-word"


You can deploy the skeleton project immediately:

    $ git push heroku master
    Writing objects: 100% (13/13), 2.87 KiB, done.
    Total 13 (delta 0), reused 0 (delta 0)

    -----> Heroku receiving push
    -----> Clojure app detected
    -----> Installing Leiningen
           Downloading: leiningen-2.0.0-preview7-standalone.jar
    [...]
    -----> Launching... done, v3
           http://hackerdict.herokuapp.com deployed to Heroku

    To git@heroku.com:hackerdict.git
     * [new branch]      master -> master

It's live! Hit it with `curl`:

    $ curl http://hackerdict.herokuapp.com
    ["Hello" :from Heroku]

The cookie-backed session store needs a session secret configured for encryption:

    $ heroku config:add SESSION_SECRET=$RANDOM_16_CHARS





## Remote REPL

The [devcenter article](https://devcenter.heroku.com/articles/debugging-clojure)
has a detailed explanation, but using the `repl` task from Leiningen
2.x lets you connect a REPL to a remote process over HTTP. The first
step is setting up credentials:

    $ heroku config:add REPL_USER=[...] REPL_PASSWORD=[...]

Then you can launch the REPL:

    $ lein repl :connect http://$REPL_USER:$REPL_PASSWORD@hackerdict.herokuapp.com/repl

Everything you enter will be evaluated remotely in the running dyno,
which can be very useful for debugging or inspecting live data.
