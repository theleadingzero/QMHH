{
	"patcher" : 	{
		"fileversion" : 1,
		"appversion" : 		{
			"major" : 5,
			"minor" : 1,
			"revision" : 9
		}
,
		"rect" : [ 25.0, 69.0, 640.0, 480.0 ],
		"bglocked" : 0,
		"defrect" : [ 25.0, 69.0, 640.0, 480.0 ],
		"openrect" : [ 0.0, 0.0, 0.0, 0.0 ],
		"openinpresentation" : 0,
		"default_fontsize" : 12.0,
		"default_fontface" : 0,
		"default_fontname" : "Arial",
		"gridonopen" : 0,
		"gridsize" : [ 15.0, 15.0 ],
		"gridsnaponopen" : 0,
		"toolbarvisible" : 1,
		"boxanimatetime" : 200,
		"imprint" : 0,
		"enablehscroll" : 1,
		"enablevscroll" : 1,
		"devicewidth" : 0.0,
		"boxes" : [ 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "/seq/stop",
					"outlettype" : [ "" ],
					"fontname" : "Arial",
					"id" : "obj-6",
					"presentation_rect" : [ 413.0, 391.0, 0.0, 0.0 ],
					"fontsize" : 12.0,
					"patching_rect" : [ 420.0, 390.0, 59.0, 18.0 ],
					"numinlets" : 2,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "/seq/start",
					"outlettype" : [ "" ],
					"fontname" : "Arial",
					"id" : "obj-5",
					"presentation_rect" : [ 326.0, 395.0, 0.0, 0.0 ],
					"fontsize" : 12.0,
					"patching_rect" : [ 330.0, 390.0, 60.0, 18.0 ],
					"numinlets" : 2,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "/seq/setAngle 2.",
					"outlettype" : [ "" ],
					"fontname" : "Arial",
					"id" : "obj-4",
					"presentation_rect" : [ 324.0, 271.0, 0.0, 0.0 ],
					"fontsize" : 12.0,
					"patching_rect" : [ 330.0, 270.0, 97.0, 18.0 ],
					"numinlets" : 2,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "message",
					"text" : "/seq/setDuration 4.",
					"outlettype" : [ "" ],
					"fontname" : "Arial",
					"id" : "obj-3",
					"fontsize" : 12.0,
					"patching_rect" : [ 300.0, 240.0, 111.0, 18.0 ],
					"numinlets" : 2,
					"numoutlets" : 1
				}

			}
, 			{
				"box" : 				{
					"maxclass" : "newobj",
					"text" : "udpsend 127.0.0.1 3334",
					"fontname" : "Arial",
					"id" : "obj-1",
					"fontsize" : 12.0,
					"patching_rect" : [ 300.0, 330.0, 140.0, 20.0 ],
					"numinlets" : 1,
					"numoutlets" : 0
				}

			}
 ],
		"lines" : [ 			{
				"patchline" : 				{
					"source" : [ "obj-6", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-5", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-4", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
, 			{
				"patchline" : 				{
					"source" : [ "obj-3", 0 ],
					"destination" : [ "obj-1", 0 ],
					"hidden" : 0,
					"midpoints" : [  ]
				}

			}
 ]
	}

}
