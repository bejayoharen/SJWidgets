SJWidgets
=========

A Swing Compatible Widget Library for Java

#What it Does#

SJWidgets is designed to make it easier to use swing to make beautiful UIs. Plugable Look and Feels does
not accomplish this task because you cannot easilly specify the look of each widget; instead it is designed
to let you change the look of all widgets. Moreover, PLaF is not particularly easy to use.

SJWidgets lets you fine-tune the look of each widget outside of code, so that you can
easilly make your UI's look like the design spec using XML rather than code. Like HTML/Javascript/CSS, this
helps to separate functionality from design.

#Status#

SJWidgets has been used in production code and is definately ready for production use. It was originally
written for the XO Wave Digital audio workstation, developed further for Indaba Music's Mantis Web Based
Digital Audio Workstation, and now developed further, again, for Xonami. However, there
are two main issues:

1. **Documentation is sparse** The Javadocs for this code is almost nonexistant.
However, because SJWidget classes extend swing widgets, once you understnad
swing and the concepts outlined here, you probably won't need any further documentation.

2. **Some code may not be actively tested.** Features that aren't actively used right now may not be tested,
either. However, much of the code is actually highly functional.

#How it Works#

SJWidgets are just like Swing widgets (in fact, they ARE swing widgets), excet with an additional argument in the constructor: a widgetID
argument. If non-null, this ID (an arbitrary string), is used to define the look of that individual widget.
For example, instead of using this code:

    JButton b = new JButton( "Push Me" );
    b.setBorder(...);
    b.setIcon(...);

You might do something like this:

    SJButton b = new SJButton( "Pushable Button" );

and define the border, label text and icon in the XML file with the ID "Pushable Button".

Layouts are still done using "classic" swing layout code, except for absolute positioning, which can be done
in the XML files. Although this violates separation of funtion and design, so far we've found it hasn't been
too much of a hinderence.


#WidgetDescriptions.xml#

Usually you will define the look and feel of each widget in a single file called WidgetDescriptions.xml.
You can call this file whatever you like, and you can have multiple files if just having one is too
unweildy -- it can be especially helpful to divide your UI into multiple files if it is complex
and contains many different widgets. Each of these files contains a single <Widgets /> element.

One time for each application, you may define a <DisableFocus /> element, which can contain the text "true"
or "false" (the default is "false"). This is useful for UIs, such as audio applications, where the standard,
default keyboard navigation usually offered simply doesn't make sense.

Two other elements which act globally, and should usually appear only once (and first), are <Colors /> and
<Fonts />. 

The <Colors /> element is useful for defining colors by name, so that you don't have to use full hex codes
like #de30ff later.  It is also useful for experimenting with color schemes. This is especially useful for UIs
that reuse a given set of colors.

    <Widgets>
      <Colors>
         <Color name="transparent" alpha="0">#000000</Color>
         <Color name="light blue">#D2E8FF</Color>
         <Color name="dark blue">#004080</Color>
         <Color name="translucent" alpha=".5">#808080</Color>
         <Color name="21 percent black" alpha=".21">#000000</Color>
         <Color name="50 percent" alpha=".5">#808080</Color>
         <Color name="shading color" alpha=".7">#101010</Color>
      </Colors>

The <Fonts /> element lets you define a default font, which is a good idea, and it also lets you import custom fonts
as well.

      <Fonts>
          <DefaultFont>Lucida-regular-12</DefaultFont>
          <!-- custom fonts are decoded as fontname-size or fontname size. built-ins are font-style-size -->
          <CustomFont name="digital_7_mono">digital-7_mono.ttf</CustomFont>
      </Fonts>

If you are planning to use SJOptionPanes, you will also want to setup the look of the Option Panes using the <OptionPane />
element. This defines the IDs of widgets to use when displaying different kinds of messages.

      <OptionPane>
           <property key="error label">Option Pane: Error Label</property>
           <property key="information label">Option Pane: Information Label</property>
           <property key="warnging label">Option Pane: Warning Label</property>
           <property key="question label">Option Pane: Question Label</property>
           <property key="basic label">Option Pane: Basic Label</property>
      </OptionPane>


Once you are setup, you can start to define some widgets. Here are some examples:

    <Widget id="Example" >
      <property key="foreground">light blue</property>
      <property key="background">#000000</property>
      <property key="text">Text</property>
      <property key="font">Lucida-REGULAR-10</property>
      <property key="border color">#ffffff</property>
      <property key="border type">solid</property>
      <property key="border size">1</property>
      <property key="icon">/path/to/icon/relative/to/this/file</property>
      <property key="backgroundTiledIcon">/path/to/icon/relative/to/this/file</property>
      <!-- sizing -->
      <Size w="30" h="40" /> <!-- This is useful in, eg, windows of fixed sizes or absolute layout. In other cases, swing may not honor it. It's best to set min/max/preferred -->
      <Minimum w="30" h="40" /> <!-- Sets the minimum layout size. -->
      <Maximum w="30" h="40" /> <!-- Sets the maximum layout size. Use the string "stretch" to get it to grow as wide or tall as you like. -->
      <Preferred w="30" h="40" /> <!-- Sets the preferred layout size. -->
      <!--  layout of this component within parent -->
      <Alignment x=".5" y=".5" /> <!-- this is useful for hbox and vbox layout. You can use just x or just y. -->
    </Widget>
    <Widget id="sample label">
      <property key="horizontal alignment">right</property>
      <property key="text">email:</property>
      <property key="border type">empty</property>
      <property key="border size">0 3</property>
    </Widget>
    <Widget id="sample with icon">
      <property key="background">transparent</property>
      <property key="foreground">Indaba Blue</property>
      <property key="icon">sessionSelection/mix.png</property>
    </Widget>
    <Widget id="sample with multiple icons">
      <property key="icon">transport/metronome_off.png</property>
      <property key="selected icon">transport/metronome_on.png</property>
    </Widget>
    <Widget id="sample with gradient">
      <property key="font">lucida-regular-7</property>
      <property key="gradient top">#717171</property>
      <property key="gradient bottom">#393939</property>
    </Widget>

For properties that are repeated in multiple widget IDs, it is possible to use a "class", which is not yet documented.

Properties are documented in <a href="doc/PROPERTIES.md">doc/PROPERTIES.md</a>

#Java Code#

Now that your looks are defined, you need to write some Java Code. First, before creating any widgets, you
need to load the widget library:

    WidgetUtil.loadDescriptions("resources/WidgetDescriptions.xml");

Then, once loaded, you can create your widgets using the IDs defined in the WidgetDescriptions.xml. Note that
if the ID is not found, your app will crash.

    SJLabel b = new SJLabel( "sample label" );


#Help System#

There is a built-in help system as well, which is not yet documented.


#Building#

Build with maven. To build and run the simple tests, use:

mvn compile exec:exec -Dsimpletest

To build and run the table test, use:

mvn compile exec:exec -Dtabletest


#Examples and screenshots#

An earlier version of SJWidgets was used in Indaba Music's Mantis: http://www.indabamusic.com/labs
The current version will be used in the upcoming version of http://www.xonami.com
You can also just look at some <a href="doc/SCREENSHOTS.md">screenshots</a>

#FAQ#

Got a question? Maybe we have an answer in the <a ref="doc/FAQ.md">FAQ</a>.

#Contributors#

Bjorn Roche and Howard Shih are the primary contributors to this project.

#License#

SJ Widgets is (c) Indaba Music and Bjorn Roche

This project is licensed under a BSD-style license. See COPYING.txt for details.

Note that some files in the examples folder and some of the dependencies may use other licenses.

#Dependencies#

SJWidgets depends on BrowserLauncher2, which is included, and JDom 1.1. You may also need felix (also included) if you
use the felix features which, at this time, are experimental.
