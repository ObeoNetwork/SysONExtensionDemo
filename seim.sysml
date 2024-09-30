library package SEIM {
    action def Function;
    allocation def AllocatedFunction {
        end function : Function;
        end component : LogicalConstituent;
    }

    interface def FunctionalFlow {
        end source : FunctionPort;
        end target : FunctionPort;
    }

    interface def LogicalFlow {
        end source : ConstituentPort;
        end target : ConstituentPort;
    }

    part def LogicalConstituent;

    port def FunctionPort;
    port def ConstituentPort;
}
