import xml.etree.ElementTree as ET

svg_file = r"d:\prodev\pulse\client\pulse.svg"
tree = ET.parse(svg_file)
root = tree.getroot()

bg_path = root[0]
fg_path1 = root[1]
fg_path2 = root[2]

bg_content = f"""<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="1024"
    android:viewportHeight="1024">
    <path
        android:fillColor="{bg_path.get('fill')}"
        android:pathData="{bg_path.get('d')}" />
</vector>"""

fg_content = f"""<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="1024"
    android:viewportHeight="1024">
    <group android:translateX="529" android:translateY="397">
        <path
            android:fillColor="{fg_path1.get('fill')}"
            android:pathData="{fg_path1.get('d')}" />
    </group>
    <group android:translateX="967" android:translateY="944">
        <path
            android:fillColor="{fg_path2.get('fill')}"
            android:pathData="{fg_path2.get('d')}" />
    </group>
</vector>"""

with open(r"d:\prodev\pulse\client\app\src\main\res\drawable\ic_launcher_background.xml", "w") as f:
    f.write(bg_content)
    
with open(r"d:\prodev\pulse\client\app\src\main\res\drawable\ic_launcher_foreground.xml", "w") as f:
    f.write(fg_content)
