; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{44731905-F9D7-458E-8CD7-601167965D08}
AppName=VLC Language Learner
AppVersion=1.0
;AppVerName=test2 1.0
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
Source: "C:\Users\Dell\Documents\workspace\VideoLAN\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\Users\Dell\Documents\workspace\basicvlcj\basic-vlcj-1.0.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Dell\Documents\workspace\basicvlcj\icon.ico"; DestDir: "{app}"; Flags: ignoreversion;
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{commonprograms}\VLC Language Learner"; Filename: "{app}\basic-vlcj-1.0.exe"; IconFilename: "{app}\icon.ico"
Name: "{commondesktop}\VLC Language Learner"; Filename: "{app}\basic-vlcj-1.0.exe"; IconFilename: "{app}\icon.ico"; Tasks: desktopicon

[Run]
Filename: "{app}\basic-vlcj-1.0.exe"; Description: "{cm:LaunchProgram,VLC Language Learner}"; Flags: nowait postinstall skipifsilent

