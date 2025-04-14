

You need "TODO Highlight" extension also

You need these in your settings.json:


    "editor.tokenColorCustomizations": {
        // This colours every comment including javascript etc, which we dont want
        //"comments": "#880000"
        // This colours only our DONE comments
        "textMateRules": [
            {
                "scope": "comment.line.done-prefix.abc",
                "settings": {
                    "foreground": "#666666"
                }
            },
            // We cannot use BACKGROUND colours for our tokens (*), so we rely on the "todohighlight" extension to do that and we
            // colour them using todohighlight.keywords below
            // (*) See thread: https://github.com/microsoft/vscode/issues/3429
            // {
            //     "scope": "comment.line.wip.abc",
            //     "settings": {
            //         "foreground": "#DD0000",
            //         "fontStyle": "bold strikethrough",
            //         "background": "#660000",         <<< NOT SUPPORTED
            //         "backgroundColor": "#660000"     <<< NOT SUPPORTED
            //     }
            // }

        ]
    },
    "todohighlight.isEnable": true,
    "todohighlight.isCaseSensitive": true,
    "todohighlight.keywords": [
        {
            "text": "wip:",
            "color": "#FFFFFF",
            "backgroundColor": "green",
            "overviewRulerColor": "green"
        },
        {
            "text": "pen:",
            "color": "#000000",
            "backgroundColor": "yellow",
            "overviewRulerColor": "yellow"
        }
    ]


