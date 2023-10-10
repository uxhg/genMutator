import logging
import sys
from enum import unique, IntEnum

logger = logging.getLogger(__name__)


@unique
class ErrorCode(IntEnum):
    OTHERS = 1
    INVALID_CMDLINE = 2
    NOT_IMPLEMENTED = 3
    USER_TERM = 4
    PATH_ERROR = 5


class ColorFormatter(logging.Formatter):
    COLORS = {
        logging.DEBUG: "\033[96m",
        logging.INFO: "\033[92m",
        logging.WARNING: "\033[93m",
        logging.ERROR: "\033[91m",
        logging.CRITICAL: "\033[01;91m\033[47m",  # bold red on white background
        'RESET': "\033[0m"
    }

    def format(self, record):
        color = self.COLORS[record.levelno]
        color_reset = self.COLORS["RESET"]
        self.datefmt = "%m-%d %H:%M:%S"
        # self._style._fmt = color + '[%(asctime)s] [%(levelname)8s] [%(name)s > %(funcName)s()] ' + color_reset + '%(message)s'
        self._style._fmt = color + '[%(asctime)s] [%(levelname)8s] ' + color_reset + '%(message)s'
        return super().format(record)


def init_logging(log_level="warning"):
    rootlogger = logging.getLogger()
    if log_level is None:
        log_level = "warning"
    numeric_level = getattr(logging, log_level.upper(), None)
    if not isinstance(numeric_level, int):
        raise ValueError('Invalid log level: %s' % log_level)
    rootlogger.setLevel(numeric_level)

    handler = logging.StreamHandler(sys.stderr)
    handler.setFormatter(ColorFormatter())
    rootlogger.addHandler(handler)
