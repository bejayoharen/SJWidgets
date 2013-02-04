SJWidgets - Properties
======================

Every widget in SJWidgets is identified with an ID. In the WidgetDescriptions.xml you can set properties
like text, border, and so on, associated with the Widget ID. This file documents all these properties.

#Basic Properties#

Most properties inside a widget are specified using the <property /> xml elment inside the <Widget /> element,
like so:

    <Widget id="Example" >
      <property key="foreground">light blue</property>
      <property key="background">#000000</property>
      ....
   <Widget />

Some important basic properties are:

 * **foreground** the foreground color.
 * **background** the background color.
 * **gradient top** a color, used in combination with gradient bottom, used to create a vertical gradient background.
 * **gradient bottom** a color, used in combination with gradient top, used to create a vertical gradient background.
 * **gradient left** a color, used in combination with gradient right, used to create a horizontal gradient background.
 * **gradient right** a color, used in combination with gradient left, used to create a horizontal gradient background.
 * **background composite** a pair of values, separated by space or comma, that define how background color and image
   should be compoited. The first value is a constant such as CLEAR, SRC, DST, or SRC_OVER
   (as defined in java.awt.AlphaComposite) and the second value is an alpha value between 0 and 1.
 * **background image** an image to use for the background.
 * **background image style** one of TILE, CENTER, CENTER_IGNORE_SIZE. Note that stretching images is not an option.
 * **type** the mime-type. *Not yet supported.*
 * **text** the text to display in the widget. Usually the text is displayed in the foreground color.
 * **font** the font to display the text in. This is parsed the same way it is in Java. For example, Lucida-REGULAR-10 refers
   to Lucida Grande, Regular typeface, size 10. For custom fonts that you've manually imported, you simply use the name and
   size, like this: NAME-10.
 * **horizontal alignment** may be one of leading, left, right, trailing, or center. These values correspond to the
   swing horizontal alignment property for widgets that support it. Basically, it aligns the entire contents of the widget
   to one side or another.
 * **horizontal text position** may be one of leading, left, right, trailing, or center. These values correspond to the
   swing horizontal text position property for widgets that support it. Basically, it defines the relationship between the
   icon and the text. this is especially useful for using an icon as the background, for example, of a button.
 * **vertical text position** may be one of top, bottom, or center. These values correspond to the
   swing vertical text position property for widgets that support it. Basically, this property defines the relationship
   between the icon and text verticaly.
 * **icon** the default icon to display. By default, this is displayed next to the text, not behind or as the background.
 * **selected icon** for widgets that support it, this is the icon that's displayed instead of the default icon
   when the widget is selected.
 * **disabled icon** for widgets that support it, this is the icon that's displayed instead of the default icon
   when the widget is disabled.
 * **disabled selected icon** for widgets that support it, this is the icon that's displayed instead of the default icon
   when the widget is disabledi and also selected.
 * **pressed icon** for widgets that support it, this is the icon that's displayed when the widget is pressed. For example,
   when a button is pressed, this icon will be displayed. By default, an icon is derived from the default icon.
 * **url** if this widget is a button, this url will be opened when pressed. Note that that includes all subclasses of
   AbstractButton, including RadioButton and so on.


#Layout#

Although complete layout via XML is not yet supported, a few layout related properties can be set. Note that
these are not "properties" in the sense of having the xml element name property, but they are still "properties"
of the widget.

For example, if you are doing absolute layout, you may want to set the size and position manually:

     <Widget id="Example" >
       <Size w="30" h="40" />
       <Position x="1" y="30" />
       ....
     <Widget />

If, instead, you are using a layout manager, you can set properties such as the minimum, maximum and preferred sizes.
It may also be useful to set the x and y alignment (note that you do not need to set both x and y alignments, one or
the other is fine). In the examples below, the word "stretch" is used to indicate that the component should grow
to fill the amount of space that's given. There is nothing magic here: it just substitutes a very large number.

    <Widget id="Example" >
      <Minimum w="30" h="40" />
      <Maximum w="30" h="stretch" />
      <Preferred w="30" h="40" />
      <Alignment x=".5" y=".5" />
      ....
    <Widget />

#Borders#

Three properties are used for creating borders. Borders are pretty basic, so if you want something fancier, you should
design it in outside software such as photoshop, save the result as an image and use it as a background.

 * **border type** one of none, solid, or empty.
 * **border size** may contain 1, 2, 3, or 4 integer values specifying the width of the border in pixels. The order and meaning of the values mimics css.
 * **border color** the color of the border. Only valid for border type solid.
