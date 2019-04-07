[Setup]
AppId={{44731905-F9D7-458E-8CD7-601167965D08}
AppName=VLC Language Learner
AppVersion=1.0
DefaultDirName=C:\Program Files\VideoLAN
DisableProgramGroupPage=yes
OutputBaseFilename=VLC-Language-Learner
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "D:\workspace\VideoLAN\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "D:\workspace\VideoLAN\basic-vlcj-1.0.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\workspace\VideoLAN\icon.ico"; DestDir: "{app}"; Flags: ignoreversion;

[Icons]
Name: "{commonprograms}\VLC Language Learner"; Filename: "{app}\basic-vlcj-1.0.jar"; IconFilename: "{app}\icon.ico"
Name: "{commondesktop}\VLC Language Learner"; Filename: "{app}\basic-vlcj-1.0.jar"; IconFilename: "{app}\icon.ico"; Tasks: desktopicon

