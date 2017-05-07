# ReactiveScrolling

Not sure if this is _the right_ way of doing it...

I have been always wondering which is the right way to do a very popular way of filling a list of items loaded from a result of API calls by pagination.

Say, calling /api?page=1 returns 10 strings. /api?page=2 does next 10 strings. One very popular way of achieving it by using RxJava2 is to create a simple Observable and ObservableOnSubscribe and call emitter.onNext() once and then emitter.onComplete().

The next page will be the same calls - you create Observable and ObservableOnSubscribe. The only difference is the query parameter (`page`). The new page is called every time RecyclerView reaches to the bottom of the list.

I was wondering how I could create an Observable only once, and every `paginated` call does `emitter.onNext()`. And after all items are retrieved (perhaps by going to page=19021), calls emitter.onComplete().

This project is an experiment of the idea. But I feel something is still wrong - is it a good idea to have two Observables, one for OnScrollChangedListener, and the other for the actual API calls? Does having the `Manager` class make sense? ...
