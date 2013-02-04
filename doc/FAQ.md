SJWidgets FAQ
============

#Why isn't SJWidgets a Look and Feel?#

The Java Pluggable Look and Feel architecture is extremely complex and powerful.
However, it is designed primarilly to replace the look and feel
of all widgets with a predefied set of looks. For example, if you want your entire UI to
have a unified look, it works well, but only so far as defining what widgets of a certain
type should look like. ie, making all your buttons look a certain way. This may work well
for simple UIs or prototypes, but it simply isn't adequate for complex, beautiful UIs. The
Java Folks obviously realized this when the built JavaFX, but for many of us, JavaFX isn't
a realistic option either. The truth is, swing is capable of building rich, beautiful UIs
but we need a good way to define the appearance of individuial widgets easilly, outside of
code. SJWidgets is designed for building UIs that require attention to detail and which may
have significant design collatoral and/or tweaking of individual widgets, rather than just
widget types.

#Where are SJTable and SJList?#

It may seem like you should be able to create an SJTable the same way you can create an
SJButton. As it so happens, Swing does not use 
