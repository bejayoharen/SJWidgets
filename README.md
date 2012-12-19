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

#How it Works#

SJWidgets are just like Swing widgets, excet with an additional argument in the constructor: a widgetID
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
unweildy. Each of these files contains a single <Widgets /> element.

Anywhere where you need a color
you can simply use a standard web color hex triplet like #de30ff, but you can also predefine some colors
that you can refer to by name, which is handy for UIs that reuse colors, or if you want to experiment with
a color scheme. This also makes it easier to work with transparencies.

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

You'll also probably want to define a default font. If you like, you can define custom fonts as well.

      <Fonts>
          <DefaultFont>Lucida-regular-12</DefaultFont>
          <!-- custom fonts are decoded as fontname-size or fontname size. built-ins are font-style-size -->
          <CustomFont name="digital_7_mono">digital-7_mono.ttf</CustomFont>
      </Fonts>

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


#Java Code#

Now that your looks are defined, you need to write some Java Code. First, before creating any widgets, you
need to load the widget library:

    WidgetUtil.loadDescriptions("resources/WidgetDescriptions.xml");

Then, once loaded, you can create your widgets using the IDs defined in the WidgetDescriptions.xml. Note that
if the ID is not found, your app will crash.

    SJLabel b = new SJLabel( "sample label" );


#Help#

There is an extensive built-in help system as well, which is not yet documented.


#Examples#

An earlier version of SJWidgets was used in Indaba Music's Mantis: http://www.indabamusic.com/labs
The current version will be used in the upcoming version of http://www.xonami.com


#License#

SJ Widgets is (c) Indaba Music and Bjorn Roche

A license has not yet been chosen for SJWidgets. It will probably be BSD or similar. If this concerns you,
you may want to hold off for now.


