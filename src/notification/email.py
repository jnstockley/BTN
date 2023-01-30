import smtplib
import ssl
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

import secrets
from youtube.uploads import YouTubeChannel


def new_upload(channels: list[YouTubeChannel]):
    sender_email = secrets.sender_email
    receiver_email = secrets.receiver_email
    password = secrets.password

    message = MIMEMultipart("alternative")

    message["From"] = sender_email
    message["To"] = receiver_email
    message["Subject"] = f"{channels[0].channel_name} has uploaded a new YouTube Video!"
    text = f"""\
        {channels[0].channel_name} has uploaded a new YouTube Video!
        {channels[0].latest_upload.title}
        https://www.youtube.com/watch?v={channels[0].latest_upload.upload_id}
        
        Diagnostic Data:
        {channels}
    """

    html = f"""\
    <html>
        <body>
            {channels[0].channel_name} has uploaded a new YouTube Video!
            </br>
            {channels[0].latest_upload.title}
            </br>
            <a href=https://www.youtube.com/watch?v={channels[0].latest_upload.upload_id}>
            https://www.youtube.com/watch?v={channels[0].latest_upload.upload_id}</a>
            </br>
            </br>
            Diagnostic Data:
            {channels}
        </body>
    </html>
    """

    part1 = MIMEText(text, "plain")
    part2 = MIMEText(html, "html")

    message.attach(part1)
    message.attach(part2)

    context = ssl.create_default_context()
    with smtplib.SMTP_SSL("smtp.gmail.com", 465, context=context) as server:
        server.login(sender_email, password)
        server.sendmail(
            sender_email, receiver_email, message.as_string()
        )
